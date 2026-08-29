import { Navigate } from "react-router-dom";
import { getToken } from "./api/cliente";
import type React from "react";

export function RotaProtegida({ children }: { children: React.ReactNode }) {
    if (!getToken()) {
        return <Navigate to="/" replace/>
    }

    return children;
}