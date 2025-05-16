# backend/app/database/test_conn.py
import os
from dotenv import load_dotenv
import pymysql
from sqlalchemy import create_engine

# fuerza la carga del .env de la raíz
load_dotenv(os.path.join(os.path.dirname(__file__), "../../.env"))

host = os.getenv("DB_HOST")
user = os.getenv("DB_USER")
pwd  = os.getenv("DB_PASSWORD")
name = os.getenv("DB_NAME")
port = int(os.getenv("DB_PORT"))

print("→ PyMySQL.connect() test")
try:
    conn = pymysql.connect(host=host, user=user, password=pwd, database=name, port=port)
    print("   ✅ PyMySQL se conectó correctamente")
    conn.close()
except Exception as e:
    print("   ❌ PyMySQL ERROR:", e)

print("\n→ SQLAlchemy.create_engine() test")
url = f"mysql+pymysql://{user}:{pwd}@{host}:{port}/{name}"
try:
    engine = create_engine(url, pool_pre_ping=True)
    with engine.connect() as conn2:
        print("   ✅ SQLAlchemy se conectó correctamente")
except Exception as e:
    print("   ❌ SQLAlchemy ERROR:", e)
