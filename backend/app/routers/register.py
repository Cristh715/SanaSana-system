from fastapi import APIRouter, Depends
from app.database.session import SessionLocal
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.paciente import Paciente as PacienteSchema
from app.schemas.auth import Token

from app.services.paciente_service import register_paciente

router = APIRouter(prefix="/api", tags=["pacientes"])

async def get_db():
    async with SessionLocal() as session:
        yield session

@router.post("/register", response_model=Token)
async def create_paciente(data: PacienteSchema, db: AsyncSession = Depends(get_db)):
    return await register_paciente(db, data)
