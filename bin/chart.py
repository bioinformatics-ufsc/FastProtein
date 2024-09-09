#!/usr/bin/python3
import sys
import os
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

output_file =sys.argv[1]

print("FastProtein chart generation script")
print("Processing file " + output_file)

dir_temp = os.path.dirname(output_file)
print("Temporary dir " + dir_temp)
dir_image = os.path.join(dir_temp, "image")
if not os.path.exists(dir_image):
    os.mkdir(dir_image)
print("Destination dir: " +dir_image)
#Generating charts
print("Creating Dispersion ph x kda")
plt.clf()
output = pd.read_csv(output_file, sep='\t')
dados = output.loc[:,['kDa','Isoelectric_Point']]
print(dados)
sns.set_palette("pastel")
plt.figure(figsize=(10,8))
ax = sns.jointplot(x="Isoelectric_Point", y="kDa", data=dados, kind='scatter')
ax.fig.suptitle('Dispersion - Isoelectric point (p.H) X "Molecular weight (kDa)"', fontsize=20, y=1.05)
ax.set_axis_labels("Isoelectric point (p.H)", "Molecular weight (kDa)", fontsize=14)
plt.savefig(os.path.join(dir_image,'kda-vs-pi.png'))
plt.savefig(os.path.join(dir_image,'kda-vs-pi-300dpi.png'), dpi=300)
print("Chart created in "+os.path.join(dir_image,'kda-vs-pi.png'))
print("Chart created in "+os.path.join(dir_image,'kda-vs-pi-300dpi.png'))

print("Subcellular localization bar")
df = pd.DataFrame(output['Localization'])
df = pd.DataFrame({'Localization': df['Localization'].unique(), 'Proteins': df['Localization'].value_counts()})
df = df.reset_index(drop=True)
df = df.sort_values(by='Localization')
df = df.reset_index(drop=True)
print(df)

plt.clf()
sns.set_palette("pastel")
plt.figure(figsize=(10,8))
sns.barplot(data=df, x="Localization", y="Proteins")
plt.xticks(rotation=25)
for index, row in df.iterrows():
    plt.annotate(row['Proteins'], xy=(index, row['Proteins']), ha='center', xytext=(0,3), textcoords='offset points')
plt.title('Subcellular Localization', fontdict={"fontsize":20})
plt.xlabel('')
plt.ylabel('Proteins', fontsize=18)
plt.tick_params(axis='both', which='major', labelsize=12)
plt.savefig(os.path.join(dir_image,'subcell-resume-bar.png'))
plt.savefig(os.path.join(dir_image,'subcell-resume-bar-300dpi.png'), dpi=300)
