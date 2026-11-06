import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/features/auth/AuthContext";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { LogOut, ShieldCheck, UserRound, ScrollText, CircleHelp } from "lucide-react";

function PlayerDashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [signingOut, setSigningOut] = useState(false);
  const [error, setError] = useState("");

  const loginId = user?.signInDetails?.loginId;
  const username = user?.username || "Player";
  const displayName = loginId || username;
  const initials = displayName.slice(0, 2).toUpperCase();

  async function handleLogout() {
    setSigningOut(true);
    setError("");
    try {
      await logout();
      navigate("/login", { replace: true });
    } catch (logoutError) {
      setError(logoutError?.message || "Unable to sign out. Please try again.");
    } finally {
      setSigningOut(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-5 sm:px-8">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-white"><ShieldCheck size={23} /></div>
            <div><p className="font-bold tracking-wide text-slate-900">SENTINEL</p><p className="text-xs text-slate-500">Player portal</p></div>
          </div>
          <div className="flex items-center gap-3">
            <div className="hidden text-right sm:block"><p className="max-w-56 truncate text-sm font-medium text-slate-800">{displayName}</p><p className="text-xs text-slate-500">Player</p></div>
            <Button variant="outline" onClick={handleLogout} disabled={signingOut}>
              <LogOut className="mr-2 h-4 w-4" />{signingOut ? "Signing out…" : "Sign out"}
            </Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl space-y-6 px-5 py-8 sm:px-8">
        <section className="rounded-2xl bg-slate-950 p-7 text-white sm:p-10">
          <Badge className="border border-indigo-400/30 bg-indigo-500/15 text-indigo-200">PLAYER ACCOUNT</Badge>
          <h1 className="mt-5 text-3xl font-bold tracking-tight sm:text-4xl">Welcome, {displayName}</h1>
          <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">
            This is your Sentinel player portal. Your account is signed in and ready.
          </p>
        </section>

        {error && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</p>}

        <div className="grid gap-5 lg:grid-cols-3">
          <Card className="lg:col-span-1">
            <CardHeader><CardTitle className="flex items-center gap-2 text-base"><UserRound size={18} className="text-indigo-600" />Account</CardTitle></CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-100 font-semibold text-indigo-700">{initials}</div>
                <div className="min-w-0"><p className="truncate font-medium text-slate-900">{displayName}</p><p className="text-sm text-slate-500">Signed in with Sentinel</p></div>
              </div>
              <div className="space-y-3 border-t pt-4 text-sm">
                <div className="flex justify-between gap-3"><span className="text-slate-500">Username</span><span className="truncate font-medium text-slate-800">{username}</span></div>
                {loginId && <div className="flex justify-between gap-3"><span className="text-slate-500">Email</span><span className="truncate font-medium text-slate-800">{loginId}</span></div>}
                <div className="flex justify-between gap-3"><span className="text-slate-500">Session</span><Badge className="bg-green-100 text-green-700">Active</Badge></div>
              </div>
            </CardContent>
          </Card>

          <Card className="lg:col-span-2">
            <CardHeader><CardTitle className="flex items-center gap-2 text-base"><ScrollText size={18} className="text-indigo-600" />Player activity</CardTitle></CardHeader>
            <CardContent>
              <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 px-5 py-10 text-center">
                <CircleHelp className="mx-auto h-8 w-8 text-slate-400" />
                <h2 className="mt-3 font-semibold text-slate-800">Your activity will appear here</h2>
                <p className="mx-auto mt-2 max-w-lg text-sm leading-6 text-slate-500">
                  Player specific case, sanction, and appeal history is not exposed by the current API. Once those endpoints are available, this panel can show your live account activity.
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}

export default PlayerDashboard;
