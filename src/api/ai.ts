import request from './axios'

export interface AIMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

export interface AIChatRequest {
  message: string
  history?: AIMessage[]
}

export interface AIChatResponse {
  reply: string
  timestamp: number
}

export const chatApi = {
  sendMessage(data: AIChatRequest) {
    return request.post<any, any>('/ai/chat', data)
  }
}
