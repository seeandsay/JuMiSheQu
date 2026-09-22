import request from './request'

export function getFeed(params) {
  return request({
    url: '/review/feed',
    method: 'get',
    params
  })
}

export function createReview(data) {
  return request({
    url: '/review',
    method: 'post',
    data
  })
}

export function getMyReviews(params) {
  return request({
    url: '/review/my',
    method: 'get',
    params
  })
}
