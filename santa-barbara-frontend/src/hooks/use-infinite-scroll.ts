import { useEffect, useRef } from 'react'

interface UseInfiniteScrollOptions {
  onLoadMore: () => void

  enabled: boolean

  itemCount: number
}


export function useInfiniteScroll({ onLoadMore, enabled, itemCount }: UseInfiniteScrollOptions) {
  const sentinel = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const element = sentinel.current
    if (!element || !enabled) return


    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) onLoadMore()
      },
      { rootMargin: '200px' },
    )

    observer.observe(element)
    return () => observer.disconnect()
  }, [onLoadMore, enabled, itemCount])

  return sentinel
}
