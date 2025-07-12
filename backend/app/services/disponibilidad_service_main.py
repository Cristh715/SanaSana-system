from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.orm import selectinload
from sqlalchemy import distinct, func
from app.models.medico import Medico
from app.models.turno import Turno
from app.schemas.disponibilidad import (
    EspecialidadResponse,
    MedicoPorTurnoResponse,
    EspecialidadDisponible,
    MedicoDisponible,
    HorarioDisponible
)
from typing import List, Optional, Tuple

class DisponibilidadService:
    def __init__(self, db: AsyncSession):
        self.db = db

    async def get_all_specialties(self) -> List[EspecialidadResponse]:
        result = await self.db.execute(select(distinct(Medico.especialidad)))
        specialties = result.scalars().all()
        return [EspecialidadResponse(especialidad=s) for s in specialties if s is not None]

    async def get_medicos_by_especialidad_and_turno(
        self,
        especialidad: str,
        turno_nombre: str
    ) -> List[MedicoPorTurnoResponse]:
        """
        Obtiene los médicos que tienen un turno disponible para una especialidad y nombre de turno dados,
        incluyendo el ID del turno.
        """
        query = (
            select(Medico, Turno.id_turno) 
            .join(Turno, Medico.id_medico == Turno.id_medico)
            .where(
                Medico.especialidad == especialidad,
                Turno.turno == turno_nombre
            )
        )
        result = await self.db.execute(query)
        
        medicos_con_turno_ids = result.unique().all()

        response_medicos: List[MedicoPorTurnoResponse] = []
        for medico, id_turno in medicos_con_turno_ids:
            full_name = f"Dr. {medico.nombres} {medico.apellidos}"
            response_medicos.append(
                MedicoPorTurnoResponse(
                    id_medico=medico.id_medico,
                    id_turno=id_turno,
                    nombres=medico.nombres,
                    apellidos=medico.apellidos,
                    especialidad=medico.especialidad,
                    nombre_completo=full_name,
                    correo=medico.correo
                )
            )
        return response_medicos

    async def obtener_especialidades_unicas_compat(self) -> List[str]:
        result = await self.db.execute(select(Medico.especialidad).distinct())
        return [row[0] for row in result.fetchall()]

    async def obtener_turnos_por_especialidad_compat(self, especialidad: str) -> List[Tuple[int, str]]:
        stmt = (
            select(Turno.id_turno, Turno.turno)
            .join(Medico, Medico.id_medico == Turno.id_medico)
            .filter(Medico.especialidad == especialidad)
            .distinct()
        )
        result = await self.db.execute(stmt)
        return result.fetchall()

    async def obtener_medico_por_turno_compat(self, id_turno: int) -> Optional[Medico]:
        stmt = (
            select(Medico)
            .join(Turno, Turno.id_medico == Medico.id_medico)
            .filter(Turno.id_turno == id_turno)
        )
        result = await self.db.execute(stmt)
        return result.scalar_one_or_none()

    async def obtener_horarios_disponibles_compat(self) -> List[EspecialidadDisponible]:
        result = await self.db.execute(select(Medico.especialidad).distinct())
        especialidades = [row[0] for row in result.fetchall()]
        respuesta = []
        for especialidad in especialidades:
            medicos_result = await self.db.execute(
                select(Medico).filter(Medico.especialidad == especialidad)
            )
            medicos = medicos_result.scalars().all()
            medicos_data = []
            for medico in medicos:
                turnos_result = await self.db.execute(
                    select(Turno).filter(Turno.id_medico == medico.id_medico)
                )
                turnos = turnos_result.scalars().all()
                horarios = [
                    HorarioDisponible(turno=turno.turno)
                    for turno in turnos
                ]
                medicos_data.append(
                    MedicoDisponible(
                        nombre_completo=f"{medico.nombres} {medico.apellidos}",
                        horarios=horarios
                    )
                )
            respuesta.append(
                EspecialidadDisponible(
                    especialidad=especialidad,
                    medicos=medicos_data
                )
            )
        return respuesta