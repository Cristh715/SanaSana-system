from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.paciente import PacienteCreate, TokenResponse
from app.services.paciente_service import register_paciente
from app.database.session import SessionLocal

router = APIRouter(prefix="/api", tags=["pacientes"])

async def get_db():
    async with SessionLocal() as session:
        yield session

@router.post("/register", response_model=TokenResponse)
async def create_paciente(data: PacienteCreate, db: AsyncSession = Depends(get_db)):
    return await register_paciente(db, data)
