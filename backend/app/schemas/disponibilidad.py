from pydantic import BaseModel, Field
from typing import List, Optional

class EspecialidadInput(BaseModel):
    especialidad: str

class TurnoInput(BaseModel):
    id_turno: int

class HorarioDisponible(BaseModel):
    turno: str

class MedicoDisponible(BaseModel):
    nombre_completo: str
    horarios: List[HorarioDisponible]

class EspecialidadDisponible(BaseModel):
    especialidad: str
    medicos: List[MedicoDisponible]

class EspecialidadResponse(BaseModel):
    especialidad: str

    class Config:
        from_attributes = True

class MedicoPorTurnoResponse(BaseModel):
    id_medico: int
    id_turno: int
    nombres: str
    apellidos: str
    especialidad: str
    nombre_completo: str 
    correo: Optional[str] = None

    class Config:
        from_attributes = True

class MedicosDisponiblesResponse(BaseModel):
    medicos: List[MedicoPorTurnoResponse]

class MedicoDetalleResponse(BaseModel):
    id_medico: int
    nombres: str
    apellidos: str
    correo: Optional[str] = None
    especialidad: str

    class Config:
        from_attributes = True