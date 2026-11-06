import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { RefreshCw } from "lucide-react";
import { caseService } from "@/features/cases/caseService";
import { auditService } from "./auditService";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

function formatDate(value) {
  if (!value) return "-";
  return new Date(value).toLocaleString("en-IN");
}

function Audit() {
  const [selectedCaseId, setSelectedCaseId] = useState("");
  const casesQuery = useQuery({
    queryKey: ["audit-cases"],
    queryFn: caseService.getAllCases,
  });
  const cases = casesQuery.data || [];
  const caseId = selectedCaseId || cases[0]?.caseId || "";
  const auditQuery = useQuery({
    queryKey: ["case-audit", caseId],
    queryFn: () => auditService.getCaseAudit(caseId),
    enabled: Boolean(caseId),
  });
  const events = auditQuery.data || [];

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div><h1 className="text-2xl font-bold text-slate-900">Audit History</h1><p className="mt-1 text-sm text-slate-500">Review recorded changes for a case.</p></div>
        <Button variant="outline" onClick={() => auditQuery.refetch()} disabled={!caseId || auditQuery.isFetching}><RefreshCw className={`mr-2 h-4 w-4 ${auditQuery.isFetching ? "animate-spin" : ""}`} /> Refresh</Button>
      </div>
      <Card>
        <CardHeader><CardTitle className="text-base">Select case</CardTitle></CardHeader>
        <CardContent>
          {casesQuery.isLoading ? <p className="text-sm text-slate-500">Loading cases…</p>
            : casesQuery.isError ? <p role="alert" className="text-sm text-red-600">Could not load cases: {casesQuery.error?.message}</p>
              : cases.length === 0 ? <p className="text-sm text-slate-500">No cases are available for audit review.</p>
                : <select aria-label="Case" value={caseId} onChange={(event) => setSelectedCaseId(event.target.value)} className="h-10 w-full max-w-md rounded-md border border-input bg-background px-3 text-sm"><option value="" disabled>Select a case</option>{cases.map((item) => <option key={item.caseId} value={item.caseId}>{item.caseId} · {item.status}</option>)}</select>}
        </CardContent>
      </Card>
      <Card>
        <CardHeader><CardTitle className="text-base">Case activity</CardTitle></CardHeader>
        <CardContent>
          {auditQuery.isLoading ? <p className="py-8 text-center text-sm text-slate-500">Loading audit history…</p>
            : auditQuery.isError ? <div className="space-y-3 py-8 text-center"><p className="text-red-600">Failed to load audit history.</p><p className="text-sm text-slate-500">{auditQuery.error?.message}</p><Button variant="outline" onClick={() => auditQuery.refetch()}>Retry</Button></div>
              : !caseId ? <p className="py-8 text-center text-sm text-slate-500">Choose a case to view its audit history.</p>
                : events.length === 0 ? <p className="py-8 text-center text-sm text-slate-500">No audit events recorded for this case.</p>
                  : <div className="overflow-x-auto"><table className="w-full text-sm"><thead><tr className="border-b text-left text-xs text-slate-500"><th className="pb-3 pr-4 font-medium">Date</th><th className="pb-3 pr-4 font-medium">Action</th><th className="pb-3 pr-4 font-medium">Change</th><th className="pb-3 font-medium">Performed by</th></tr></thead><tbody>{events.map((event) => <tr key={event.auditId} className="border-b last:border-0"><td className="whitespace-nowrap py-4 pr-4 text-slate-500">{formatDate(event.createdAt)}</td><td className="py-4 pr-4 font-medium text-slate-800">{event.action?.replaceAll("_", " ") || "-"}</td><td className="py-4 pr-4 text-slate-600">{event.oldValue || "∅"} → {event.newValue || "∅"}</td><td className="py-4 text-slate-600">{event.performedBy || "-"}</td></tr>)}</tbody></table></div>}
        </CardContent>
      </Card>
    </div>
  );
}

export default Audit;
