import { create } from "zustand";
import { jwtDecode } from "jwt-decode";
import type { JwtPayload } from "../types/jwt";

interface AuthState {
  accessToken: string | null;
  user: JwtPayload | null;

  isAuthenticated: () => boolean;
  hasRole: (role: string) => boolean;

  login: (token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => {
  const token = localStorage.getItem("accessToken");
  let decodedUser: JwtPayload | null = null;

  if (token) {
    try {
      decodedUser = jwtDecode<JwtPayload>(token);
    } catch {
      localStorage.removeItem("accessToken");
    }
  }

  return {
    accessToken: token,
    user: decodedUser,

    isAuthenticated: () => !!get().accessToken,

    hasRole: (role: string) => {
      const user = get().user;
      if (!user) return false;
      return user.roles.includes(role as any);
    },

    login: (token: string) => {
      localStorage.setItem("accessToken", token);
      const decoded = jwtDecode<JwtPayload>(token);

      set({
        accessToken: token,
        user: decoded,
      });
    },

    logout: () => {
      localStorage.removeItem("accessToken");
      set({
        accessToken: null,
        user: null,
      });
    },
  };
});