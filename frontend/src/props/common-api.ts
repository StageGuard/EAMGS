export interface CommonApi {
  info: string | null,
  ranking: string | null,
  profile: string | null,
  customize: {
    get: string | null,
    update: string | null
  }
}
