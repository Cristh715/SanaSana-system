# backend/app/services/paciente_service.py

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.exc import IntegrityError
from fastapi import HTTPException, status

from app.models.cuenta_paciente import CuentaPaciente
from app.models.paciente import Paciente
from app.schemas.paciente import PacienteCreate, TokenResponse
from app.utils.reniec import validar_dni
from app.utils.security import hash_password
from app.auth.jwt import create_access_token


async def register_paciente(db: AsyncSession, data: PacienteCreate) -> TokenResponse:
    # 1) Validar DNI en RENIEC
    reniec = await validar_dni(data.dni, data.digitoVerificador)
    if not reniec:
        raise HTTPException(status_code=400, detail="DNI no válido.")

    # 2) Validar nombres/apellidos sin depender del orden
    api_words   = set( reniec["nombreCompleto"].upper().split() )
    input_words = set(
        f"{data.nombres} {data.apellidoPaterno} {data.apellidoMaterno}"
        .upper()
        .split()
    )
    if api_words != input_words:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=("Datos inválidos , coloque correctamente la información")
        )

    # 3) Verificar que las contraseñas coincidan
    if data.password != data.password_confirm:
        raise HTTPException(status_code=400, detail="Contraseñas no coinciden.")

    # 4) Crear la cuenta de paciente
    cuenta = CuentaPaciente(
        correo=data.correo,
        contrasena_hash=hash_password(data.password)
    )
    db.add(cuenta)
    try:
        await db.commit()
    except IntegrityError:
        await db.rollback()
        raise HTTPException(status_code=400, detail="El correo ya está registrado.")
    await db.refresh(cuenta)

    # 5) Crear el registro de paciente
    paciente = Paciente(
        id_cuenta=cuenta.id_cuenta,
        nombres=data.nombres,
        apellidos=f"{data.apellidoPaterno} {data.apellidoMaterno}",
        dni=data.dni,
        telefono=data.telefono,
        fecha_nacimiento=data.fecha_nacimiento
    )
    db.add(paciente)
    await db.commit()
    await db.refresh(paciente)

    # 6) Generar y devolver token JWT
    token = create_access_token({"sub": str(cuenta.id_cuenta)})
    return {"access_token": token, "token_type": "bearer"}
