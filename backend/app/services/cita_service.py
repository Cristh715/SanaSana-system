# backend/app/services/cita_service.py

from datetime import datetime
from fastapi import HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.turno import Turno
from app.models.cita import Cita
from sqlalchemy import select, func

async def solicitar_cita(db: AsyncSession, id_paciente: int, id_turno: int, sintomas: str):
    turno = await db.get(Turno, id_turno)
    if not turno or turno.cupo <= 0:
        raise HTTPException(status_code=400, detail="Turno no disponible")

    total_citas = await db.execute(
        select(func.count()).select_from(Cita).where(Cita.id_turno == id_turno)
    )
    numero_turno = total_citas.scalar() + 1

    cita = Cita(
        id_paciente=id_paciente,
        id_turno=id_turno,
        especialidad=turno.medico.especialidad,
        fecha=turno.fecha,
        numero_turno=numero_turno,
        sintomas=sintomas,
        estado="pendiente",
        fecha_creacion=datetime.now(),
        confirmacion_asistencia=0
    )
    db.add(cita)
    turno.cupo -= 1
    await db.commit()
    return {"mensaje": "Cita registrada con éxito", "numero_turno": numero_turno}