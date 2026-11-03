import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

import AppLayout from "@/components/layout/AppLayout";

import Dashboard from "@/features/dashboard/Dashboard";
import Cases from "@/features/cases/Cases";
import Players from "@/features/players/Players";
import Audit from "@/features/audit/Audit";
import Settings from "@/features/settings/Settings";

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/cases" element={<Cases />} />
          <Route path="/players" element={<Players />} />
          <Route path="/audit" element={<Audit />} />
          <Route path="/settings" element={<Settings />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;
