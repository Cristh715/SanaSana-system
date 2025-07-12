# app/Models/registro_bienestar.py
from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey
from sqlalchemy.sql import func
from sqlalchemy.orm import relationship
from app.database.session import Base

class RegistroBienestar(Base):
    __tablename__ = "registro_bienestar"

    id_registro_bienestar = Column(Integer, primary_key=True, index=True, autoincrement=True)
    id_paciente = Column(Integer, ForeignKey("pacientes.id_paciente"), nullable=False)
    fecha_registro = Column(DateTime, server_default=func.now())
    estado_emocional = Column(String(50), nullable=False)
    sintomas_fisicos = Column(Text, nullable=True) # JSON string
    nota_adicional = Column(Text, nullable=True)

    paciente = relationship("Paciente", back_populates="registros_bienestar")

    def __repr__(self):
        return f"<RegistroBienestar(id={self.id_registro_bienestar}, paciente={self.id_paciente}, emocion='{self.estado_emocional}')>"