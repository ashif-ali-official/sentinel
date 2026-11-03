
export const dashboardStats = [
  {
    title: "Total Cases",
    value: "1,284",
    change: "+12.5%",
    description: "vs. previous month",
    trend: "up",
  },
  {
    title: "Pending Cases",
    value: "246",
    change: "-8.2%",
    description: "vs. previous month",
    trend: "down",
  },
  {
    title: "In Progress",
    value: "128",
    change: "+4.3%",
    description: "vs. previous month",
    trend: "up",
  },
  {
    title: "Resolved Cases",
    value: "910",
    change: "+18.6%",
    description: "vs. previous month",
    trend: "up",
  },
];

export const casesByStatus = [
  { name: "Pending", value: 246 },
  { name: "In Progress", value: 128 },
  { name: "Resolved", value: 910 },
];

export const casesBySeverity = [
  { name: "Critical", cases: 35 },
  { name: "High", cases: 120 },
  { name: "Medium", cases: 285 },
  { name: "Low", cases: 844 },
];

export const recentCases = [
  {
    id: "CASE-1024",
    player: "Player_Alpha",
    category: "Harassment",
    severity: "Critical",
    status: "In Progress",
    date: "Nov 04, 2026",
  },
  {
    id: "CASE-1023",
    player: "ShadowHunter",
    category: "Abusive Language",
    severity: "High",
    status: "Pending",
    date: "Nov 04, 2026",
  },
  {
    id: "CASE-1022",
    player: "GamingPro99",
    category: "Cheating",
    severity: "High",
    status: "Resolved",
    date: "Nov 03, 2026",
  },
  {
    id: "CASE-1021",
    player: "NightWolf",
    category: "Spam",
    severity: "Low",
    status: "Resolved",
    date: "Nov 03, 2026",
  },
  {
    id: "CASE-1020",
    player: "DragonSlayer",
    category: "Threats",
    severity: "Critical",
    status: "Pending",
    date: "Nov 02, 2026",
  },
];

export const recentActivity = [
  {
    moderator: "Alex Morgan",
    action: "Resolved a moderation case",
    reference: "CASE-1022",
    time: "10 minutes ago",
  },
  {
    moderator: "Sarah Wilson",
    action: "Assigned a case",
    reference: "CASE-1024",
    time: "35 minutes ago",
  },
  {
    moderator: "John Carter",
    action: "Issued a player sanction",
    reference: "PLAYER-2041",
    time: "1 hour ago",
  },
  {
    moderator: "Alex Morgan",
    action: "Updated case severity",
    reference: "CASE-1023",
    time: "2 hours ago",
  },
];
