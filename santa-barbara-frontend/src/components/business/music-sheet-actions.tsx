import { Button } from '#/components/ui/button'
import { FilePlusIcon, FileTextIcon } from 'lucide-react'


export function MusicSheetActions() {
  return (
    <div className="flex items-center gap-2">
      <Button type="button" variant="outline" size="sm">
        <FileTextIcon />
        Partituras
      </Button>
      <Button type="button" variant="outline" size="sm">
        <FilePlusIcon />
        Adicionar partituras
      </Button>
    </div>
  )
}
