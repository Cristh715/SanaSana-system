# app/Models/medico.py
from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from app.database.session import Base

class Medico(Base):
    __tablename__ = "medicos"

    id_medico = Column(Integer, primary_key=True, index=True)
    nombres = Column(String(100))
    apellidos = Column(String(100))
    especialidad = Column(String(100))
    correo = Column(String(150))

    turnos = relationship("Turno", back_populates="medico")

    def __repr__(self):
        return f"<Medico(id={self.id_medico}, nombre='{self.nombres} {self.apellidos}', especialidad='{self.especialidad}')>"