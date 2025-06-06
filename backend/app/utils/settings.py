from dotenv import load_dotenv 
import os

load_dotenv()

# Database configuration
DB_HOST = os.getenv('DB_HOST')
DB_USER = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_NAME = os.getenv('DB_NAME')
DB_PORT = os.getenv('DB_PORT') 

# JWT configuration
JWT_SECRET = os.getenv("JWT_SECRET")
JWT_ALGORITHM = os.getenv("JWT_ALGORITHM")
JWT_EXPIRE_MINUTES = int(os.getenv("JWT_EXPIRE_MINUTES", 20))

# RENIEC token
RENIEC_URL=os.getenv("RENIEC_URL")
RENIEC_TOKEN = os.getenv("RENIEC_TOKEN")