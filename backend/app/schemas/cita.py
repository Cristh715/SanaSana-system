from pydantic import BaseModel
from datetime import date

class CitaCreate(BaseModel):
    id_paciente: int
    id_turno: int
    sintomas: str

class CitaResponse(BaseModel):
    mensaje: str
    numero_turno: int