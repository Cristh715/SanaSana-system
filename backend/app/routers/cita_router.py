from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas.cita import CitaCreate, CitaResponse
from app.services.cita_service import solicitar_cita, obtener_historial_citas
from app.database.session import SessionLocal
from app.auth.jwt import get_current_user_id  

cita_router = APIRouter(prefix="/api", tags=["citas"])

async def get_db():
    async with SessionLocal() as session:
        yield session

@cita_router.post("/citas", response_model=CitaResponse, status_code=status.HTTP_201_CREATED)
async def create_cita(
    data: CitaCreate,
    db: AsyncSession = Depends(get_db),
    id_cuenta: int = Depends(get_current_user_id) 
):
    try:
        return await solicitar_cita(
            db,
            id_cuenta=id_cuenta,
            id_turno=data.id_turno,
            fecha=data.fecha,
            sintomas=data.sintomas
        )
    except Exception as e:
        print(f"Error al crear cita: {e}")
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Error interno al crear la cita.")

@cita_router.get("/citas/historial")
async def get_historial_citas(
    db: AsyncSession = Depends(get_db),
    id_cuenta: int = Depends(get_current_user_id)
):
    return await obtener_historial_citas(db, id_cuenta)