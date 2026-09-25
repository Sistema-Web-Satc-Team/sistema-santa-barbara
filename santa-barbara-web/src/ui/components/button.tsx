import "@/ui/styles/button.css";
import { cn } from "@/ui/utils/cn";
import { cva, type VariantProps } from "class-variance-authority";
import * as React from "react";
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
  size?: "default" | "icon-xs" | "icon-sm";
  render?: React.ReactElement;
}

export function Button({
  onClick,
  variant,
  children,
  disabled,
  className,
  size = "default",
  render,
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

  const button = (
    <button
      onClick={handleClick}
      className={cn(buttonVariants({ variant, isPressed: isPressed, className }), {
        "size-6 p-0": size === "icon-xs",
        "size-8 p-0": size === "icon-sm",
      })}
      disabled={disabled || isPressed}
      {...props}
    >
      {children}
    </button>
  );

  return render ? React.cloneElement(render, button.props, children) : button;
}