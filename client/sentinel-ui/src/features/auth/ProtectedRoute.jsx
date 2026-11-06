import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./AuthContext";

function ProtectedRoute({allowedRoles}) {
    const { isAuthenticated, loading, role } = useAuth();

    if(loading){
        return (
            <div className="flex min-h-screen item-center justify-center">
                <p className="text-sm text-slate-500">
                    Checking authentication...
                </p>
            </div>
        );
    }

    if(!isAuthenticated){
        return <Navigate to="/login" replace />;
    }

    if (!role || !allowedRoles.includes(role)) {
        return <Navigate to="/unauthorized" replace />;
    }

    return <Outlet />;
}

export default ProtectedRoute;