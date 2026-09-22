import os

from dotenv import load_dotenv

load_dotenv()

# 数据库
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "3306"))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("DB_NAME", "drama_community")

# DeepSeek（文本）
DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "")
DEEPSEEK_BASE_URL = os.getenv("DEEPSEEK_BASE_URL", "https://api.deepseek.com")
DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")

# 硅基流动（图像生成）
SILICONFLOW_API_KEY = os.getenv("SILICONFLOW_API_KEY", "")
SILICONFLOW_BASE_URL = os.getenv("SILICONFLOW_BASE_URL", "https://api.siliconflow.cn/v1")
SILICONFLOW_IMAGE_MODEL = os.getenv("SILICONFLOW_IMAGE_MODEL", "black-forest-labs/FLUX.1-schnell")
SILICONFLOW_IMAGE_SIZE = os.getenv("SILICONFLOW_IMAGE_SIZE", "768x1024")

# AI 生成海报的本地保存目录（前端 public 目录下）
AGENT_DIR = os.path.dirname(os.path.abspath(__file__))
POSTER_SAVE_DIR = os.getenv(
    "POSTER_SAVE_DIR",
    os.path.join(AGENT_DIR, "..", "drama-web", "public", "images", "ai-posters"),
)
