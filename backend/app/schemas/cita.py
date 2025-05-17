from pydantic import BaseModel
from datetime import date, datetime

class CitaCreate(BaseModel):
    id_paciente: int
    id_turno: int
    sintomas: str

class CitaResponse(BaseModel):
    id_cita: int
    id_paciente: int
    id_turno: int
    especialidad: str
    fecha: date
    numero_turno: int
    sintomas: str
    estado: str
    qr_codigo: str
    fecha_creacion: datetime
    confirmacion_asistencia: bool

    class Config:
        from_attributes = True  # ✅ Requerido para .from_orm()