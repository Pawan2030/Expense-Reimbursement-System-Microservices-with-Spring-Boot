export type JwtClaims = {
  sub?: string;
  username?: string;
  role?: 'EMPLOYEE' | 'MANAGER';
  employeeId?: number;
  managerId?: number;
  exp?: number;
  iat?: number;
};

function base64UrlDecode(input: string): string {
  // add padding if missing
  const pad = input.length % 4;
  const base64 = (input + (pad ? '='.repeat(4 - pad) : ''))
    .replace(/-/g, '+')
    .replace(/_/g, '/');
  return atob(base64);
}

export function decodeJwt(token: string): JwtClaims | null {
  try {
    const payload = token.split('.')[1];
    const json = base64UrlDecode(payload);
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function isExpired(claims: JwtClaims | null): boolean {
  if (!claims?.exp) return true;
  const now = Math.floor(Date.now() / 1000);
  return claims.exp < now;
}
