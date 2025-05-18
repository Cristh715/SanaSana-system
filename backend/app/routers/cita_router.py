from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.cita import CitaCreate, CitaResponse
from app.services.cita_service import solicitar_cita
from app.database.session import SessionLocal
from app.auth.jwt import get_current_user_id  

cita_router = APIRouter(prefix="/api", tags=["citas"])

async def get_db():
    async with SessionLocal() as session:
        yield session

@cita_router.post("/citas", response_model=CitaResponse)
async def create_cita(
    data: CitaCreate,
    db: AsyncSession = Depends(get_db),
    id_cuenta: int = Depends(get_current_user_id)  
):
    return await solicitar_cita(
        db,
        id_cuenta=id_cuenta,
        id_turno=data.id_turno,
        sintomas=data.sintomas
    )