from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.ext.asyncio import AsyncSession
from app.database.session import SessionLocal
from app.services import disponibilidad_service_main as disponibilidad_service

async def get_db():
    async with SessionLocal() as session:
        yield session

router = APIRouter(prefix="/disponibilidad", tags=["Disponibilidad"])

class EspecialidadInput(BaseModel):
    especialidad: str

class TurnoInput(BaseModel):
    id_turno: int

@router.post("/especialidades")
async def obtener_especialidades(db: AsyncSession = Depends(get_db)):
    return await disponibilidad_service.obtener_especialidades_unicas(db)

@router.post("/turnos")
async def obtener_turnos(input_data: EspecialidadInput, db: AsyncSession = Depends(get_db)):
    turnos = await disponibilidad_service.obtener_turnos_por_especialidad(db, input_data.especialidad)
    return [{"id_turno": t.id_turno, "turno": t.turno} for t in turnos]

@router.post("/medico")
async def obtener_medico(input_data: TurnoInput, db: AsyncSession = Depends(get_db)):
    medico = await disponibilidad_service.obtener_medico_por_turno(db, input_data.id_turno)
    if not medico:
        raise HTTPException(status_code=404, detail="Médico no encontrado")
    return {
        "id_medico": medico.id_medico,
        "nombres": medico.nombres,
        "apellidos": medico.apellidos,
        "correo": medico.correo,
        "especialidad": medico.especialidad
    }

@router.get("/horarios-disponibles")
async def obtener_horarios_disponibles(db: AsyncSession = Depends(get_db)):
    return await disponibilidad_service.obtener_horarios_disponibles(db)