from fastapi import HTTPException, Depends

from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select

from app.schemas.auth import LoginRequest, LoginResponse, Token

from app.models.cuenta_paciente import CuentaPaciente
from app.models.paciente import Paciente

from app.utils.hash import verify_password
from app.auth.jwt   import create_access_token

async def find_account_by_email(db: AsyncSession, email: str) -> CuentaPaciente:
    '''
    Find account by email.
    '''
    
    try:
        query = await db.execute(select(CuentaPaciente).where(CuentaPaciente.correo == email)) 
        account = query.scalar_one_or_none()
        
    except Exception as e:
        raise HTTPException(status_code=500, detail="Database error: " + str(e))

    if not account:
        raise HTTPException(status_code=404, detail="Account not found")
    
    return CuentaPaciente (
        id_cuenta=account.id_cuenta,
        correo=account.correo,
        contrasena_hash=account.contrasena_hash,
        cuenta_bloqueada=account.cuenta_bloqueada,
        fecha_registro=account.fecha_registro
    )

async def authenticate_user(login_request: LoginRequest, db: AsyncSession) -> LoginResponse:
    '''
    Authenticate user and return JWT token.     
    '''
    
    # Find account by email
    account = await find_account_by_email(db, login_request.email)
    
    if not account:
        raise HTTPException(status_code=404, detail="Account not found")
    
    # Verify password
    hashed_password = account.contrasena_hash

    if not verify_password(login_request.password, hashed_password):
        raise HTTPException(status_code=401, detail="Incorrect Password")

    if account.cuenta_bloqueada:
        raise HTTPException(status_code=401, detail="Account is blocked")

    # Find patient by account id
    query = await db.execute(select(Paciente).where(Paciente.id_cuenta == account.id_cuenta))
    patient = query.scalar_one_or_none()
    
    # Generate JWT token
    token = Token(
        access_token=create_access_token(data={"sub": str(account.id_cuenta)}),
        token_type="bearer"
    )

    # Return response
    return {
        "dni": str(patient.dni),
        "email": account.correo, 
        "token": token
    }