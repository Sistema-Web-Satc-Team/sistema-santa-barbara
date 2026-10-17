/** Minúsculas e sem acentos, para comparar textos ("Música" == "musica"). */
export function normalizeText(value: string): string {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
}

export function uniqueText(values: string[]): string[] {
  const seen = new Set<string>()
  const result: string[] = []

  for (const value of values) {
    const key = normalizeText(value)
    if (key === '' || seen.has(key)) continue
    seen.add(key)
    result.push(value)
  }

  return result
}
