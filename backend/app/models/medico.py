from sqlalchemy import Column, Integer, String
from app.database.session import Base

class Medico(Base):
    __tablename__ = "medicos"

    id_medico = Column(Integer, primary_key=True, index=True)
    nombres = Column(String(100))
    apellidos = Column(String(100))
    especialidad = Column(String(100))
    correo = Column(String(150))