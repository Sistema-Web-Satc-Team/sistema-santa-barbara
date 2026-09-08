import "@/ui/styles/button.css";
import { cn } from "@/ui/utils/cn";
import { cva, type VariantProps } from "class-variance-authority";
import { useState } from "react";

const buttonVariants = cva("button", {
  variants: {
    variant: {
      normal: "button-normal",
      outline: "button-outline",
      ghost: "button-ghost",
    },
    isPressed: {
      true: "",
      false: "",
    },
  },
  compoundVariants: [
    {
      variant: "normal",
      isPressed: true,
      class: "button-normal--pressed",
    },
    {
      variant: "outline",
      isPressed: true,
      class: "button-outline--pressed",
    },
    {
      variant: "ghost",
      isPressed: true,
      class: "button-ghost--pressed",
    },
  ],
  defaultVariants: {
    variant: "normal",
    isPressed: false,
  },
});

export interface ButtonProps
  extends React.ComponentProps<"button">,
    VariantProps<typeof buttonVariants> {
  onClick?: () => Promise<void> | void;
}

export function Button({
  onClick,
  variant,
  children,
  disabled,
  className,
  ...props
}: ButtonProps) {
  const [isPressed, setIsPressed] = useState(false);

  async function handleClick() {
    if (isPressed) return;

    try {
      setIsPressed(true);
      if (onClick) await onClick();
    } finally {
      setIsPressed(false);
    }
  }

  return (
    <button
      onClick={handleClick}
      className={cn(buttonVariants({ variant, isPressed: isPressed, className }))}
      disabled={disabled || isPressed}
      {...props}
    >
      {children}
    </button>
  );
}