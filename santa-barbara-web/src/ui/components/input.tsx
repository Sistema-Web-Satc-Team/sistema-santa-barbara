import "@/ui/styles/input.css";
import { cn } from "@/ui/utils/cn.ts";
import { Input as InputPrimitive } from "@base-ui/react/input";
import { cva, type VariantProps } from "class-variance-authority";
import * as React from "react";

const inputVariants = cva("input", {
  variants: {
    variant: {
      normal: "input-normal",
      discreet: "input-discreet",
      disabled: "input--disabled"
    },
  }
})

export interface InputProps
  extends React.ComponentProps<"input">,
    VariantProps<typeof inputVariants> { 
}


function Input({ variant, className, type, ...props }: InputProps) {
  return (
    <InputPrimitive
      type={type}
      data-slot="input"
      className={cn(inputVariants({ variant, className }))}
      {...props}
    />
  )
}

export { Input };

