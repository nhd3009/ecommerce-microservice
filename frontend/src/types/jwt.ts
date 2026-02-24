export const ROLES = {
  ADMIN: "ROLE_ADMIN",
  USER: "ROLE_USER",
  EMPLOYEE: "ROLE_EMPLOYEE",
} as const;

export type Role = typeof ROLES[keyof typeof ROLES];

export interface JwtPayload {
  sub: string;
  username: string;
  email: string;
  roles: Role[];
  iat: number;
  exp: number;
}

export const isTokenExpired = (exp: number): boolean => {
  const now = Math.floor(Date.now() / 1000);
  return exp < now;
};