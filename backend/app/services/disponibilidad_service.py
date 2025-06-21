from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.medico import Medico
from app.models.turno import Turno

async def obtener_especialidades_unicas(db: AsyncSession):
    result = await db.execute(select(Medico.especialidad).distinct())
    return [row[0] for row in result.fetchall()]

async def obtener_turnos_por_especialidad(db: AsyncSession, especialidad: str):
    stmt = (
        select(Turno.id_turno, Turno.turno)
        .join(Medico, Medico.id_medico == Turno.id_medico)
        .filter(Medico.especialidad == especialidad)
        .distinct()
    )
    result = await db.execute(stmt)
    return result.fetchall()

async def obtener_medico_por_turno(db: AsyncSession, id_turno: int):
    stmt = (
        select(Medico)
        .join(Turno, Turno.id_medico == Medico.id_medico)
        .filter(Turno.id_turno == id_turno)
    )
    result = await db.execute(stmt)
    return result.scalar_one_or_none()