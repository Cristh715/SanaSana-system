from sqlalchemy import Column, Integer, String, ForeignKey, Date
from sqlalchemy.orm import relationship
from app.database.session import Base

class Paciente(Base):
    __tablename__ = "pacientes"

    id_paciente = Column(Integer, primary_key=True, index=True)
    id_cuenta = Column(Integer, ForeignKey("cuenta_paciente.id_cuenta"), nullable=False)
    nombres = Column(String(100), nullable=False)
    apellidos = Column(String(200), nullable=False)  # ahora un solo campo
    dni = Column(String(8), unique=True, index=True, nullable=False)
    telefono = Column(String(20), nullable=True)
    fecha_nacimiento = Column(Date, nullable=True)

    cuenta = relationship("CuentaPaciente", backref="pacientes")
    registros_bienestar = relationship("RegistroBienestar", back_populates="paciente")
