import asyncio
import sys
import argparse
from f2.apps.douyin.handler import DouyinHandler
from f2.log.logger import logger
import json

# 解决 GBK 编码问题
sys.stdout.reconfigure(encoding="utf-8")
logger.setLevel('ERROR')
async def main(cookie: str, aweme_id: str):
    kwargs = {
        "headers": {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36 Edg/130.0.0.0",
            "Referer": "https://www.douyin.com/",
        },
        "cookie": cookie,
        "proxies": {"http://": None, "https://": None},
    }
    
    handler = DouyinHandler(kwargs)
    setattr(handler, "enable_bark", False)
    
    video = await handler.fetch_one_video(aweme_id=aweme_id)
    jsonres = {
        "cover":[video.cover],
        "aweme_id":video.aweme_id,
        "desc":video.desc,
        "video_play_addr":json.dumps(video.video_play_addr),
        "nickname":video.nickname,
        "uid":video.uid,
        "create_time":video.create_time
    }
    print(jsonres)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Fetch video from Douyin")
    parser.add_argument("--cookie", type=str, required=True, help="Douyin cookie")
    parser.add_argument("--aweme_id", type=str, required=True, help="Aweme ID of the video")
    
    args = parser.parse_args()
    
    asyncio.run(main(args.cookie, args.aweme_id))
