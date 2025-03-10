"use client"

import { useState } from "react"
import Link from "next/link"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { Button } from "@/components/ui/button"
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { toast } from "@/hooks/use-toast"

const formSchema = z.object({
  email: z.string().email({
    message: "Por favor ingresa un correo electrónico válido.",
  }),
})

export default function RecoverPasswordPage() {
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
    },
  })

  function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true)

    // Simulación de recuperación de contraseña
    setTimeout(() => {
      console.log(values)
      toast({
        title: "Solicitud enviada",
        description: "Revisa tu correo electrónico para restablecer tu contraseña.",
      })
      setIsSubmitted(true)
      setIsLoading(false)
    }, 1000)
  }

  if (isSubmitted) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-md mx-auto space-y-6">
          <div className="text-center space-y-4">
            <h1 className="text-3xl font-bold">Revisa tu correo</h1>
            <p className="text-muted-foreground">
              Hemos enviado un enlace para restablecer tu contraseña a tu correo electrónico. Por favor revisa tu
              bandeja de entrada y sigue las instrucciones.
            </p>
            <Button asChild className="mt-4">
              <Link href="/login">Volver a inicio de sesión</Link>
            </Button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-md mx-auto space-y-6">
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold">Recuperar contraseña</h1>
          <p className="text-muted-foreground">Ingresa tu correo electrónico para recibir instrucciones</p>
        </div>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Correo electrónico</FormLabel>
                  <FormControl>
                    <Input type="email" placeholder="ejemplo@correo.com" {...field} />
                  </FormControl>
                  <FormDescription>Te enviaremos un enlace para restablecer tu contraseña.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Enviando..." : "Enviar instrucciones"}
            </Button>
          </form>
        </Form>

        <div className="text-center">
          <p className="text-sm text-muted-foreground">
            <Link href="/login" className="text-primary hover:underline">
              Volver a inicio de sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

