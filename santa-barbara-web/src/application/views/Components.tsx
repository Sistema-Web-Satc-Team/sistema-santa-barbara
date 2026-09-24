import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";
import { Card} from "@/ui/components/card";
import { Avatar} from "@/ui/components/avatar";

function Components() {
    return (
        <div className="flex flex-col w-[80vw] mx-auto">
            <h2 className="my-8">Colors:</h2>
            
            <div className="flex flex-row gap-4">
                <div className="w-12.5 h-12.5 bg-(--light-neutral-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--neutral-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--foreground-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--strong-foreground-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--strong-surface-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--surface-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--brand-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--press-brand-color) rounded-lg" />
                <div className="w-12.5 h-12.5 bg-(--hover-brand-color) rounded-lg" />
            </div>


            <h2 className="my-8">Buttons: </h2>

            <div className="flex flex-row gap-4">
                <Button 
                    variant="normal"
                    type="submit" 
                    onClick={() => alert("Testando")} 
                >
                    Hello World!
                </Button>

                <Button 
                    variant="outline" 
                    type="submit" 
                    onClick={() => alert("Testando")} 
                >
                    Hello World!
                </Button>

                <Button 
                    variant="normal" 
                    type="submit" 
                    disabled
                    onClick={() => alert("Testando")} 
                >
                    Hello World!
                </Button>


                <Button 
                    variant="outline" 
                    type="submit" 
                    disabled
                    onClick={() => alert("Testando")} 
                >
                    Hello World!
                </Button>
            </div>


            <h2 className="my-8">Input: </h2>

            <div className="flex flex-col w-full gap-8">
                <Input variant="normal" placeholder="Testing" className="w-full"/>
                <Input variant="normal" placeholder="Testing" className="w-full" disabled/>

            </div>

            <h2 className="my-8">Card test: </h2>

                <div className="p-8 bg-(--strong-surface-color) rounded-lg">
            <Card>
                <h2>Testando Card</h2>
            </Card>
        </div>
            <h2 className="my-8">Avatar test: </h2>

            <div className="flex flex-row gap-4">
                <Avatar />
               
            </div>

        </div>
    )
}

export default Components;