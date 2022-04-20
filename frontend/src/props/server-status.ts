export interface ServerStatus {
  online: boolean | null,
  dbStatus: boolean | null,
  startupEpochSecond: number | null,
  profileCount: number | null
}
