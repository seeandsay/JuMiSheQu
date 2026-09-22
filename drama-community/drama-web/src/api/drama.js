import request from './request'

export function searchDrama(keyword) {
  return request({
    url: '/drama/search',
    method: 'get',
    params: { keyword }
  })
}

export function createDrama(data) {
  return request({
    url: '/drama',
    method: 'post',
    data
  })
}

export function getDramaDetail(id) {
  return request({
    url: `/drama/${id}`,
    method: 'get'
  })
}

export function getDramaReviews(id, params) {
  return request({
    url: `/drama/${id}/reviews`,
    method: 'get',
    params
  })
}
