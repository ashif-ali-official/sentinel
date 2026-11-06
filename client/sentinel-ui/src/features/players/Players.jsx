import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { RefreshCw, Users } from "lucide-react";
import { playerService } from "./playerService";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

const PAGE_SIZE = 20;

function Players() {
  const [page, setPage] = useState(0);
  const { data, isLoading, isError, error, refetch, isFetching } = useQuery({
    queryKey: ["players", page],
    queryFn: () => playerService.getPlayers({ page, size: PAGE_SIZE }),
  });
  const players = data?.content || [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Player Management</h1>
          <p className="mt-1 text-sm text-slate-500">View player profiles and account status.</p>
        </div>
        <Button variant="outline" onClick={() => refetch()} disabled={isFetching}>
          <RefreshCw className={`mr-2 h-4 w-4 ${isFetching ? "animate-spin" : ""}`} /> Refresh
        </Button>
      </div>

      <Card>
        <CardContent className="flex items-center gap-4 p-5">
          <div className="rounded-xl bg-indigo-100 p-3"><Users className="h-6 w-6 text-indigo-600" /></div>
          <div><p className="text-sm text-slate-500">Total players</p><p className="text-2xl font-bold text-slate-900">{isLoading ? "…" : data?.totalElements ?? 0}</p></div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader><CardTitle className="text-base">Players</CardTitle></CardHeader>
        <CardContent>
          {isLoading ? <p className="py-10 text-center text-sm text-slate-500">Loading players…</p>
            : isError ? <div className="space-y-3 py-10 text-center"><p className="font-medium text-red-600">Failed to load players.</p><p className="text-sm text-slate-500">{error?.message || "Check the Player Service connection."}</p><Button variant="outline" onClick={() => refetch()}>Retry</Button></div>
              : players.length === 0 ? <p className="py-10 text-center text-sm text-slate-500">No players found.</p>
                : <div className="overflow-x-auto"><table className="w-full text-sm"><thead><tr className="border-b text-left text-xs text-slate-500"><th className="pb-3 pr-4 font-medium">Player ID</th><th className="pb-3 pr-4 font-medium">Player</th><th className="pb-3 pr-4 font-medium">Email</th><th className="pb-3 pr-4 font-medium">Region</th><th className="pb-3 pr-4 font-medium">Status</th><th className="pb-3 font-medium">Joined</th></tr></thead><tbody>
                  {players.map((player) => <tr key={player.playerId} className="border-b last:border-0 hover:bg-slate-50"><td className="whitespace-nowrap py-4 pr-4 font-medium text-indigo-600">{player.playerId}</td><td className="whitespace-nowrap py-4 pr-4"><p className="font-medium text-slate-800">{player.displayName || player.username}</p>{player.displayName && <p className="text-xs text-slate-500">{player.username}</p>}</td><td className="py-4 pr-4 text-slate-600">{player.email || "-"}</td><td className="py-4 pr-4 text-slate-600">{player.region || "-"}</td><td className="py-4 pr-4"><Badge className={player.status === "ACTIVE" ? "bg-green-100 text-green-700" : "bg-slate-100 text-slate-700"}>{player.status || "UNKNOWN"}</Badge></td><td className="whitespace-nowrap py-4 text-slate-500">{player.createdAt ? new Date(player.createdAt).toLocaleDateString("en-IN") : "-"}</td></tr>)}
                </tbody></table></div>}
          {!isLoading && !isError && (data?.totalPages || 0) > 1 && <div className="mt-5 flex items-center justify-between border-t pt-4"><p className="text-sm text-slate-500">Page {page + 1} of {data.totalPages} · {data.totalElements} players</p><div className="flex gap-2"><Button variant="outline" size="sm" disabled={page === 0 || isFetching} onClick={() => setPage((current) => current - 1)}>Previous</Button><Button variant="outline" size="sm" disabled={data.last || isFetching} onClick={() => setPage((current) => current + 1)}>Next</Button></div></div>}
        </CardContent>
      </Card>
    </div>
  );
}

export default Players;
