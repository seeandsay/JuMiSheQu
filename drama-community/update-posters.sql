-- 将测试剧集的海报改为本地图片路径（前端 public/images/banners/ 目录）
USE drama_community;

UPDATE drama SET poster = '/images/banners/甄嬛传.jpeg'     WHERE name = '甄嬛传';
UPDATE drama SET poster = '/images/banners/琅琊榜.jpg'       WHERE name = '琅琊榜';
UPDATE drama SET poster = '/images/banners/狂飙.jpg'         WHERE name = '狂飙';
UPDATE drama SET poster = '/images/banners/三体.jpeg'        WHERE name = '三体';
UPDATE drama SET poster = '/images/banners/繁花.jpg'         WHERE name = '繁花';
UPDATE drama SET poster = '/images/banners/漫长的季节.jpg'   WHERE name = '漫长的季节';
