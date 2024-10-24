class AlgoritmoBanquero:
    def __init__(self):
        self.day = 0
        self.month = 0
        self.IVA = 0
        self.comission = 0
        self.total = 0
        self.maxLimit = 0.10
        self.mediaArmonica = 0
        self.factorAjuste = 0
        self.AdjustedMediaArmonica = 0
    
    def calculateNewQuantity (self, day, month, IVA, comission, total):
        self.day = day
        self.month = month
        self.IVA = IVA
        self.comission = comission
        self.total = total
        #Calculo del limite máximo
        self.maxLimit *= self.total

        #Calculo de la media armónica

        self.calculateMediaArmonica()

        #Calculo del factor de ajuste

        self.calcuteFactorAjuste()

        #Calculo de la media armonica ajustada

        return self.AdjustMediaArmonica()



    def calculateMediaArmonica(self):

        if self.IVA + self.comission > 0:
            self.mediaArmonica = 2 / (1/self.IVA + 1/self.comission)
        else:
            self.mediaArmonica = 0

        return self.mediaArmonica

    def calcuteFactorAjuste(self):
        self.factorAjuste = (self.day + self.month) / 100
        return self.factorAjuste
    
    def AdjustMediaArmonica(self):
        self.AdjustedMediaArmonica = self.factorAjuste * self.mediaArmonica
        return self.AdjustedMediaArmonica


