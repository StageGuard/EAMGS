import { CommonApi } from '@/props/common-api'
export interface GameInfo {
  id: string;
  name: string;
  supportedVersions: string[];
  api: CommonApi;
  otherApi: Map<string, string>
}
