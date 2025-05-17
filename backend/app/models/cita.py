from sqlalchemy import Column, Integer, ForeignKey, String, Date, Text, TIMESTAMP, Boolean
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.database.session import Base

class Cita(Base):
    __tablename__ = "citas"

    id_cita = Column(Integer, primary_key=True, index=True)
    id_paciente = Column(Integer, ForeignKey("pacientes.id_paciente"), nullable=False)
    id_turno = Column(Integer, ForeignKey("turnos.id_turno"), nullable=False)

    especialidad = Column(String(100), nullable=False)
    fecha = Column(Date, nullable=False)
    numero_turno = Column(Integer, nullable=False)
    sintomas = Column(Text, nullable=True)
    estado = Column(String(20), nullable=False, default="pendiente")  
    qr_codigo = Column(Text, nullable=True)
    fecha_creacion = Column(TIMESTAMP, server_default=func.current_timestamp(), nullable=False)
    confirmacion_asistencia = Column(Boolean, default=False)

    paciente = relationship("Paciente", backref="citas")
    turno = relationship("Turno", backref="citas")
