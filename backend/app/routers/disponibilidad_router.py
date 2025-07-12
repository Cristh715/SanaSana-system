from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from typing import List
from app.database.session import get_db
from app.schemas.disponibilidad import (
    EspecialidadInput,
    TurnoInput,
    EspecialidadResponse,
    MedicoPorTurnoResponse,
    MedicosDisponiblesResponse,
    EspecialidadDisponible,
    MedicoDetalleResponse
)
from app.services.disponibilidad_service_main import DisponibilidadService

router = APIRouter(
    prefix="/disponibilidad",
    tags=["Disponibilidad"]
)

@router.post("/especialidades", response_model=List[str])
async def obtener_especialidades_post(db: AsyncSession = Depends(get_db)):
    service = DisponibilidadService(db)
    return await service.obtener_especialidades_unicas_compat()

@router.post("/turnos", response_model=List[dict])
async def obtener_turnos_post(input_data: EspecialidadInput, db: AsyncSession = Depends(get_db)):
    service = DisponibilidadService(db)
    turnos = await service.obtener_turnos_por_especialidad_compat(db, input_data.especialidad)
    return [{"id_turno": t[0], "turno": t[1]} for t in turnos]

@router.post("/medico", response_model=MedicoDetalleResponse) # Devuelve MedicoDetalleResponse
async def obtener_medico_post(input_data: TurnoInput, db: AsyncSession = Depends(get_db)):
    service = DisponibilidadService(db)
    medico = await service.obtener_medico_por_turno_compat(db, input_data.id_turno)
    if not medico:
        raise HTTPException(status_code=404, detail="Médico no encontrado")
    return MedicoDetalleResponse.from_orm(medico)

@router.get("/horarios-disponibles", response_model=List[EspecialidadDisponible])
async def obtener_horarios_disponibles_get(db: AsyncSession = Depends(get_db)):
    service = DisponibilidadService(db)
    return await service.obtener_horarios_disponibles_compat()

@router.get("/especialidades", response_model=List[EspecialidadResponse])
async def get_especialidades_get(
    db: AsyncSession = Depends(get_db)
):
    service = DisponibilidadService(db)
    try:
        return await service.get_all_specialties()
    except Exception as e:
        print(f"Error al obtener especialidades: {e}")
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=f"Error: {e}")

@router.get("/medicos", response_model=MedicosDisponiblesResponse)
async def get_medicos_por_especialidad_y_turno_get(
    especialidad: str,
    turno: str,
    db: AsyncSession = Depends(get_db)
):
    service = DisponibilidadService(db)
    try:
        medicos = await service.get_medicos_by_especialidad_and_turno(especialidad, turno)
        return MedicosDisponiblesResponse(medicos=medicos)
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=f"Error al obtener médicos: {e}")