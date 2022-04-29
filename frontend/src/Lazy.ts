/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
export interface Lazy<T> {
  (): T;

  isLazy: boolean;
}

export const lazy = <T>(getter: () => T): Lazy<T> => {
  let evaluated = false
  let _res: T | null = null
  const res = <Lazy<T>> function (): T {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    if (evaluated) return _res!
    _res = getter()
    evaluated = true
    return _res
  }
  res.isLazy = true
  return res
}
