
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import { useAuth } from "@/features/auth/AuthContext";

import AppLayout from "@/components/layout/AppLayout";

import Dashboard from "@/features/dashboard/Dashboard";
import Cases from "@/features/cases/Cases";
import Players from "@/features/players/Players";
import PlayerDashboard from "@/features/players/PlayerDashboard";
import Audit from "@/features/audit/Audit";
import Settings from "@/features/settings/Settings";
import Login from "@/features/auth/Login";
import ProtectedRoute from "@/features/auth/ProtectedRoute";

function getDashboardPath(role) {
  switch (role) {
    case "ADMIN":
    case "MODERATOR":
      return "/dashboard";
    case "PLAYER":
      return "/player/dashboard";
    default:
      return "/unauthorized";
  }
}

function LoginRoute() {
  const { isAuthenticated, loading, role } = useAuth();

  if (loading) {
    return <div>Checking authentication...</div>;
  }

  if (isAuthenticated) {
    return <Navigate to={getDashboardPath(role)} replace />;
  }

  return <Login />;
}

function HomeRoute() {
  const { isAuthenticated, loading, role } = useAuth();

  if (loading) {
    return <div>Checking authentication...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Navigate to={getDashboardPath(role)} replace />;
}

function Unauthorized() {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <h1 className="text-2xl font-bold">Access denied</h1>
        <p className="mt-2 text-slate-500">
          Your account does not have permission to access this page.
        </p>
      </div>
    </div>
  );
}

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginRoute />} />
        <Route path="/" element={<HomeRoute />} />
        <Route path="/unauthorized" element={<Unauthorized />} />

        {/* Moderator portal */}
        <Route
          element={
            <ProtectedRoute allowedRoles={["MODERATOR", "ADMIN"]} />
          }
        >
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/cases" element={<Cases />} />
            <Route path="/players" element={<Players />} />
            <Route path="/audit" element={<Audit />} />
          </Route>
        </Route>

        {/* Admin-only portal settings */}
        <Route element={<ProtectedRoute allowedRoles={["ADMIN"]} />}>
          <Route element={<AppLayout />}>
            <Route path="/settings" element={<Settings />} />
          </Route>
        </Route>

        {/* Player portal */}
        <Route
          path="/player/dashboard"
          element={
            <ProtectedRoute allowedRoles={["PLAYER"]} />
          }
        >
          <Route
            index
            element={<PlayerDashboard />}
          />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;
