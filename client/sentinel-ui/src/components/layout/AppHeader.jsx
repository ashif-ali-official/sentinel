
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Bell,
  Search,
  ChevronDown,
  LogOut,
} from "lucide-react";

import { useAuth } from "@/features/auth/AuthContext";

function AppHeader() {
  const navigate = useNavigate();
  const { user, role, logout } = useAuth();

  const [signingOut, setSigningOut] = useState(false);
  const [error, setError] = useState("");

  const displayRole = {
    ADMIN: "Admin",
    MODERATOR: "Moderator",
    PLAYER: "Player",
  };

  const displayName =
    user?.signInDetails?.loginId ||
    user?.username ||
    "User";

  const initials = displayName
    .split("@")[0]
    .slice(0, 2)
    .toUpperCase();

  const handleLogout = async () => {
    try {
      setSigningOut(true);
      setError("");

      await logout();

      navigate("/login", { replace: true });
    } catch (err) {
      console.error("Logout failed:", err);
      setError("Unable to sign out. Please try again.");
    } finally {
      setSigningOut(false);
    }
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-8">
      <div>
        <p className="text-sm text-slate-500">
          Sentinel / Moderation
        </p>
      </div>

      <div className="flex items-center gap-6">
        {/* Search */}
        <div className="relative hidden md:block">
          <Search
            size={17}
            className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
          />

          <input
            type="text"
            placeholder="Search anything..."
            className="w-64 rounded-lg border border-slate-200 bg-slate-50 py-2 pl-10 pr-4 text-sm outline-none focus:border-indigo-500"
          />
        </div>

        {/* Notifications */}
        <button className="relative text-slate-500 hover:text-slate-900">
          <Bell size={20} />
          <span className="absolute -right-1 -top-1 h-2 w-2 rounded-full bg-red-500" />
        </button>

        {/* Profile */}
        <div className="flex items-center gap-2">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-100 text-sm font-semibold text-indigo-700">
            {initials}
          </div>

          <div className="hidden md:block">
            <p className="text-sm font-medium text-slate-700">
              {displayName}
            </p>
            <p className="text-xs text-slate-400">
              {displayRole[role] || "User"}
            </p>
          </div>

          <ChevronDown size={16} className="text-slate-400" />
        </div>

        {/* Logout */}
        <button
          type="button"
          onClick={handleLogout}
          disabled={signingOut}
          title="Sign out"
          className="flex items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-600 transition hover:border-red-200 hover:bg-red-50 hover:text-red-600 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <LogOut size={16} />
          <span className="hidden md:inline">
            {signingOut ? "Signing out..." : "Sign out"}
          </span>
        </button>

      </div>

      {error && (
        <div
          role="alert"
          className="absolute right-8 top-20 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700"
        >
          {error}
        </div>
      )}
    </header>
  );
}

export default AppHeader;
