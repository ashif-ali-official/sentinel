import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "@/features/auth/AuthContext";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { LogOut, ShieldCheck, UserRound, ScrollText, RefreshCw } from "lucide-react";
import { playerService } from "./playerService";
import { playerReportService } from "./reportService";

const PAGE_SIZE = 10;

function formatDate(value) {
  if (!value) return "-";
  return new Date(value).toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit",
  });
}

function getApiErrorMessage(error) {
  const body = error?.response?.data;
  return body?.message || body?.error || body?.detail || error?.message || "Request failed.";
}

function PlayerDashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [signingOut, setSigningOut] = useState(false);
  const [error, setError] = useState("");

  const playerQuery = useQuery({
    queryKey: ["player-profile"],
    queryFn: playerService.getCurrentPlayer,
  });
  const reportsQuery = useQuery({
    queryKey: ["player-reports", page],
    queryFn: () => playerReportService.getMyReports({ page, size: PAGE_SIZE }),
  });

  const loginId = user?.signInDetails?.loginId;
  const username = user?.username || "Player";
  const player = playerQuery.data;
  const displayName = player?.displayName || loginId || username;
  const initials = displayName.slice(0, 2).toUpperCase();
  const reports = reportsQuery.data?.content || [];

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
          <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">Your account and reports filed by you or about you.</p>
        </section>

        {error && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">{error}</p>}

        {playerQuery.isError && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-700">Could not load your player profile. {getApiErrorMessage(playerQuery.error)}</p>}

        <div className="grid gap-5 lg:grid-cols-3">
          <Card className="lg:col-span-1">
            <CardHeader><CardTitle className="flex items-center gap-2 text-base"><UserRound size={18} className="text-indigo-600" />Account</CardTitle></CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-100 font-semibold text-indigo-700">{initials}</div>
                <div className="min-w-0"><p className="truncate font-medium text-slate-900">{displayName}</p><p className="text-sm text-slate-500">{player?.username || "Sentinel player"}</p></div>
              </div>
              <div className="space-y-3 border-t pt-4 text-sm">
                <div className="flex justify-between gap-3"><span className="text-slate-500">Username</span><span className="truncate font-medium text-slate-800">{player?.username || username}</span></div>
                <div className="flex justify-between gap-3"><span className="text-slate-500">Email</span><span className="truncate font-medium text-slate-800">{player?.email || loginId || "-"}</span></div>
                <div className="flex justify-between gap-3"><span className="text-slate-500">Region</span><span className="font-medium text-slate-800">{player?.region || "-"}</span></div>
                <div className="flex justify-between gap-3"><span className="text-slate-500">Account status</span><Badge className={player?.status === "ACTIVE" ? "bg-green-100 text-green-700" : "bg-slate-100 text-slate-700"}>{playerQuery.isLoading ? "Loading" : player?.status || "Unknown"}</Badge></div>
              </div>
            </CardContent>
          </Card>

          <Card className="lg:col-span-2">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="flex items-center gap-2 text-base"><ScrollText size={18} className="text-indigo-600" />My reports {reportsQuery.data && <span className="text-sm font-normal text-slate-500">({reportsQuery.data.totalElements})</span>}</CardTitle>
              <Button variant="outline" size="sm" onClick={() => reportsQuery.refetch()} disabled={reportsQuery.isFetching}><RefreshCw className={`mr-2 h-4 w-4 ${reportsQuery.isFetching ? "animate-spin" : ""}`} />Refresh</Button>
            </CardHeader>
            <CardContent>
              {reportsQuery.isLoading ? <p className="py-8 text-center text-sm text-slate-500">Loading your reports…</p>
                : reportsQuery.isError ? <div className="py-8 text-center"><p className="font-medium text-red-600">Could not load your reports.</p><p className="mt-2 text-sm text-slate-500">{getApiErrorMessage(reportsQuery.error)}</p><Button className="mt-4" variant="outline" onClick={() => reportsQuery.refetch()}>Retry</Button></div>
                  : reports.length === 0 ? <p className="py-8 text-center text-sm text-slate-500">No reports have been filed by you or about you.</p>
                    : <div className="overflow-x-auto"><table className="w-full text-sm"><thead><tr className="border-b text-left text-xs text-slate-500"><th className="pb-3 pr-4 font-medium">Report</th><th className="pb-3 pr-4 font-medium">Your involvement</th><th className="pb-3 pr-4 font-medium">Category</th><th className="pb-3 pr-4 font-medium">Status</th><th className="pb-3 font-medium">Submitted</th></tr></thead><tbody>
                      {reports.map((report) => {
                        const filedByPlayer = report.reporterId === player?.playerId;
                        const reportedPlayer = report.reportedPlayerId === player?.playerId;
                        const involvement = filedByPlayer && reportedPlayer ? "Filed by you · About you" : filedByPlayer ? "Filed by you" : "About you";
                        return <tr key={report.reportId} className="border-b last:border-0"><td className="py-4 pr-4"><p className="whitespace-nowrap font-medium text-indigo-600">{report.reportId}</p><p className="mt-1 max-w-xs text-xs text-slate-500">{report.description}</p></td><td className="py-4 pr-4 text-slate-700">{involvement}</td><td className="py-4 pr-4 text-slate-600">{report.category?.replaceAll("_", " ") || "-"}</td><td className="py-4 pr-4"><Badge className="bg-slate-100 text-slate-700">{report.status?.replaceAll("_", " ") || "-"}</Badge></td><td className="whitespace-nowrap py-4 text-slate-500">{formatDate(report.createdAt)}</td></tr>;
                      })}
                    </tbody></table></div>}
              {!reportsQuery.isLoading && !reportsQuery.isError && (reportsQuery.data?.totalPages || 0) > 1 && <div className="mt-4 flex items-center justify-between border-t pt-4"><p className="text-sm text-slate-500">Page {page + 1} of {reportsQuery.data.totalPages}</p><div className="flex gap-2"><Button variant="outline" size="sm" disabled={page === 0 || reportsQuery.isFetching} onClick={() => setPage((current) => current - 1)}>Previous</Button><Button variant="outline" size="sm" disabled={reportsQuery.data.last || reportsQuery.isFetching} onClick={() => setPage((current) => current + 1)}>Next</Button></div></div>}
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}

export default PlayerDashboard;
