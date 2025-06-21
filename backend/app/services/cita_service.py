# backend/app/services/cita_service.py

from sqlalchemy.orm import selectinload
from sqlalchemy.future import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import func
from app.models.turno import Turno
from app.models.cita import Cita
from app.models.paciente import Paciente
from app.schemas.cita import CitaResponse
from datetime import datetime
import uuid

async def solicitar_cita(
    db: AsyncSession,
    id_cuenta: int,  
    id_turno: int,
    sintomas: str
) -> CitaResponse:
    # 0. Obtener id_paciente a partir de id_cuenta
    result = await db.execute(
        select(Paciente.id_paciente).where(Paciente.id_cuenta == id_cuenta)
    )
    id_paciente = result.scalar_one_or_none()

    if id_paciente is None:
        raise ValueError("No existe paciente asociado a la cuenta")
    
    # 1. Verificar que el turno existe y cargar el medico relacionado
    result = await db.execute(
        select(Turno)
        .options(selectinload(Turno.medico))  
        .where(Turno.id_turno == id_turno)
    )
    turno = result.scalar_one_or_none()

    if not turno:
        raise ValueError("El turno no existe")

    # 2. Verificar cuántas citas ya existen para ese turno
    result = await db.execute(
        select(func.count(Cita.id_cita)).where(Cita.id_turno == id_turno)
    )
    numero_actual = result.scalar()

    if numero_actual >= turno.cupo:
        raise ValueError("No hay cupos disponibles para este turno")

    # 3. Asignar número de turno
    numero_turno = numero_actual + 1

    # 4. Generar texto para QR
    qr_codigo = f"Cita-{uuid.uuid4()}"

    # 5. Crear instancia de cita usando especialidad del médico
    especialidad = turno.medico.especialidad if turno.medico else None

    nueva_cita = Cita(
        id_paciente=id_paciente,
        id_turno=id_turno,
        especialidad=especialidad,
        fecha=turno.fecha,
        numero_turno=numero_turno,
        sintomas=sintomas,
        qr_codigo=qr_codigo,
        fecha_creacion=datetime.now()
    )

    db.add(nueva_cita)
    await db.commit()
    await db.refresh(nueva_cita)

    return CitaResponse.from_orm(nueva_cita)