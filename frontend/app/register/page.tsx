"use client"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { Button } from "@/components/ui/button"
import { Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { toast } from "@/hooks/use-toast"

// Validación de formulario con Zod
const formSchema = z
  .object({
    firstName: z.string().min(2, {
      message: "El nombre debe tener al menos 2 caracteres.",
    }),
    lastName: z.string().min(2, {
      message: "El apellido debe tener al menos 2 caracteres.",
    }),
    email: z.string().email({
      message: "Por favor ingresa un correo electrónico válido.",
    }),
    address: z.string().min(10, {
      message: "La dirección debe tener al menos 10 caracteres.",
    }),
    birthDate: z.string().refine(
      (date) => {
        const birthDate = new Date(date)
        const today = new Date()
        const age = today.getFullYear() - birthDate.getFullYear()
        const monthDiff = today.getMonth() - birthDate.getMonth()

        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
          return age - 1 >= 18
        }

        return age >= 18
      },
      {
        message: "Debes ser mayor de 18 años para registrarte.",
      },
    ),
    password: z.string().min(8, {
      message: "La contraseña debe tener al menos 8 caracteres.",
    }),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Las contraseñas no coinciden.",
    path: ["confirmPassword"],
  })

export default function RegisterPage() {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
      address: "",
      birthDate: "",
      password: "",
      confirmPassword: "",
    },
  })

  // Modificar la función onSubmit para conectar con la API
  function onSubmit(values: z.infer<typeof formSchema>) {
    setIsLoading(true)

    // Formatear la fecha de nacimiento al formato ISO
    const birthDate = new Date(values.birthDate)

    // Crear el objeto de datos para la API según el formato requerido
    const userData = {
      nombre: values.firstName,
      apellido: values.lastName,
      email: values.email,
      password: values.password,
      direccionEnvio: values.address,
      fechaNacimiento: birthDate.toISOString(),
    }

    // Conectar con la API de registro
    fetch("http://localhost:8000/open/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Error al registrar usuario")
        }
        return response.json()
      })
      .then((data) => {
        console.log("Registro exitoso:", data)
        toast({
          title: "Registro exitoso",
          description: "Tu cuenta ha sido creada correctamente.",
        })
        router.push("/login")
      })
      .catch((error) => {
        console.error("Error al registrar:", error)
        toast({
          title: "Error al registrar",
          description: error.message || "Verifica tus datos e intenta nuevamente.",
          variant: "destructive",
        })
      })
      .finally(() => {
        setIsLoading(false)
      })
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-md mx-auto space-y-6">
        <div className="text-center space-y-2">
          <h1 className="text-3xl font-bold">Crear una cuenta</h1>
          <p className="text-muted-foreground">Completa el formulario para registrarte y comenzar a comprar</p>
        </div>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="firstName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Nombre</FormLabel>
                    <FormControl>
                      <Input placeholder="Juan" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="lastName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Apellido</FormLabel>
                    <FormControl>
                      <Input placeholder="Pérez" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Correo electrónico</FormLabel>
                  <FormControl>
                    <Input type="email" placeholder="ejemplo@correo.com" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="address"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Dirección de envío</FormLabel>
                  <FormControl>
                    <Input placeholder="Calle, número, ciudad, código postal" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="birthDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Fecha de nacimiento</FormLabel>
                  <FormControl>
                    <Input type="date" {...field} />
                  </FormControl>
                  <FormDescription>Debes ser mayor de 18 años para registrarte.</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Contraseña</FormLabel>
                  <FormControl>
                    <Input type="password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="confirmPassword"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Confirmar contraseña</FormLabel>
                  <FormControl>
                    <Input type="password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "Registrando..." : "Registrarse"}
            </Button>
          </form>
        </Form>

        <div className="text-center">
          <p className="text-sm text-muted-foreground">
            ¿Ya tienes una cuenta?{" "}
            <Link href="/login" className="text-primary hover:underline">
              Iniciar sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

