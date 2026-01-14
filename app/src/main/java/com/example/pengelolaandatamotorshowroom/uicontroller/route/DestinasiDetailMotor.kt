package com.example.pengelolaandatamotorshowroom.uicontroller.route

object DestinasiDetailMotor : DestinasiNavigasi {
    override val route = "detail_motor"
    const val MOTOR_ID = "motorId"
    val routeWithArgs = "$route/{$MOTOR_ID}"

    fun createRoute(motorId: Int) = "$route/$motorId"
}