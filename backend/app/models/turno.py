from sqlalchemy import Column, Integer, ForeignKey, Date, String
from sqlalchemy.orm import relationship
from app.database.session import Base
from app.models.medico import Medico


class Turno(Base):
    __tablename__ = "turnos"

    id_turno = Column(Integer, primary_key=True, index=True)
    id_medico = Column(Integer, ForeignKey("medicos.id_medico"), nullable=False)
    turno = Column(String(10), nullable=False)  
    cupo = Column(Integer, nullable=False)

    medico = relationship("Medico", backref="turnos")