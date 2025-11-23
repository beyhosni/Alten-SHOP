export interface User {
    id?: number;
    username: string;
    firstname: string;
    email: string;
    createdAt?: string;
    updatedAt?: string;
}

export interface RegisterRequest {
    username: string;
    firstname: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    email: string;
    username: string;
}
